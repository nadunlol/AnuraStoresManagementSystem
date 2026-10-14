package com.anurastores.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DatabaseInitializer {

    private static final String DATABASE_NAME = "anura_stores";
    private static final String APP_USER_PREFIX = "anura_app_";

    private static final String CONFIG_DIRECTORY =
            System.getenv("APPDATA") != null
                    ? System.getenv("APPDATA") + "\\AnuraStores"
                    : System.getProperty("user.home") + "\\AnuraStores";

    private static final Path CONFIG_FILE =
            Paths.get(CONFIG_DIRECTORY, "db.properties");

    private static final Path BACKUP_FILE = getApplicationDirectory()
            .resolve("anura_stores_initial.sql");

    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?useSSL=false"
            + "&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private DatabaseInitializer() {
    }
    
    private static Path getApplicationDirectory() {
        try {
            Path applicationPath = Paths.get(
                    DatabaseInitializer.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());

            if (java.nio.file.Files.isRegularFile(applicationPath)) {
                return applicationPath.getParent();
            }

            return applicationPath;

        } catch (Exception e) {
            return Paths.get(System.getProperty("user.dir"));
        }
    }
    
    public static boolean initialize() {
        try {
            // If application settings already exist, verify them.
            if (Files.exists(CONFIG_FILE)) {
                Properties settings = loadSettings();

                try (Connection connection = DriverManager.getConnection(
                        settings.getProperty("url"),
                        settings.getProperty("username"),
                        settings.getProperty("password"))) {

                    return true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            null,
                            "The saved database connection could not be used.\n"
                                    + "Please contact the application administrator.\n\n"
                                    + e.getMessage(),
                            "Database Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            JTextField adminUserField = new JTextField("root");
            JPasswordField adminPasswordField = new JPasswordField();

            Object[] fields = {
                    "Enter your MySQL administrator username:",
                    adminUserField,
                    "Enter your MySQL administrator password:",
                    adminPasswordField
            };

            int choice = JOptionPane.showConfirmDialog(
                    null,
                    fields,
                    "Anura Stores - Database Setup",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                return false;
            }

            String adminUsername = adminUserField.getText().trim();
            char[] adminPasswordChars = adminPasswordField.getPassword();
            String adminPassword = new String(adminPasswordChars);

            java.util.Arrays.fill(adminPasswordChars, '\0');

            if (adminUsername.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null, "MySQL username cannot be empty.");
                return false;
            }

            // Verify the administrator credentials.
            try (Connection connection = DriverManager.getConnection(
                    SERVER_URL, adminUsername, adminPassword)) {
                // Successful connection verifies the credentials.
            }

            boolean databaseExists = false;

            try (Connection connection = DriverManager.getConnection(
                    SERVER_URL, adminUsername, adminPassword);
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SHOW DATABASES LIKE '" + DATABASE_NAME + "'")) {

                databaseExists = result.next();
            }

            if (!databaseExists) {
                if (!Files.isRegularFile(BACKUP_FILE)) {
                    JOptionPane.showMessageDialog(
                            null,
                            "The initial database backup was not found:\n"
                                    + BACKUP_FILE.toAbsolutePath()
                                    + "\n\nPlace anura_stores_initial.sql beside "
                                    + "the application executable.",
                            "Database Backup Missing",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                Path mysqlExecutable = findMysqlExecutable();

                if (mysqlExecutable == null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Could not find mysql.exe.\n"
                                    + "Please ensure MySQL Server is installed.",
                            "MySQL Not Found",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                importBackup(mysqlExecutable, adminUsername, adminPassword);

                // Confirm that the import created the database.
                try (Connection connection = DriverManager.getConnection(
                        SERVER_URL, adminUsername, adminPassword);
                     Statement statement = connection.createStatement();
                     ResultSet result = statement.executeQuery(
                             "SHOW DATABASES LIKE '" + DATABASE_NAME + "'")) {

                    if (!result.next()) {
                        throw new IOException(
                                "The SQL import finished, but the database "
                                        + "could not be found.");
                    }
                }
            }

            // Generate unique application credentials.
            String appUsername = APP_USER_PREFIX
                    + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String appPassword = generatePassword();

            try (Connection connection = DriverManager.getConnection(
                    SERVER_URL, adminUsername, adminPassword);
                 Statement statement = connection.createStatement()) {

                statement.executeUpdate(
                        "CREATE USER '" + appUsername + "'@'localhost' "
                                + "IDENTIFIED BY '" + escapeSql(appPassword) + "'");

                statement.executeUpdate(
                        "GRANT ALL PRIVILEGES ON " + DATABASE_NAME + ".* TO '"
                                + appUsername + "'@'localhost'");
            }

            Properties settings = new Properties();
            settings.setProperty(
                    "url",
                    "jdbc:mysql://localhost:3306/" + DATABASE_NAME
                            + "?useSSL=false&allowPublicKeyRetrieval=true"
                            + "&serverTimezone=UTC");
            settings.setProperty("username", appUsername);
            settings.setProperty("password", appPassword);

            Files.createDirectories(Paths.get(CONFIG_DIRECTORY));

            try (java.io.OutputStream output =
                    Files.newOutputStream(CONFIG_FILE)) {
                settings.store(output, "Anura Stores database connection");
            }

            JOptionPane.showMessageDialog(
                    null,
                    databaseExists
                            ? "Database already existed. Its records were not imported or overwritten."
                                    + "\nApplication database access has been configured."
                            : "Database initialization completed successfully.",
                    "Anura Stores Setup",
                    JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Database initialization failed.\n\n"
                            + e.getMessage(),
                    "Setup Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static Properties loadSettings() throws IOException {
        Properties settings = new Properties();

        try (java.io.InputStream input = Files.newInputStream(CONFIG_FILE)) {
            settings.load(input);
        }

        return settings;
    }

    private static Path findMysqlExecutable() throws IOException {
        String programFiles = System.getenv("ProgramFiles");

        if (programFiles != null) {
            Path mysqlFolder = Paths.get(programFiles, "MySQL");

            if (Files.isDirectory(mysqlFolder)) {
                try (Stream<Path> entries = Files.list(mysqlFolder)) {
                    java.util.List<Path> folders = entries
                            .filter(Files::isDirectory)
                            .sorted()
                            .collect(java.util.stream.Collectors.toList());

                    for (Path folder : folders) {
                        Path executable = folder.resolve("bin").resolve("mysql.exe");

                        if (Files.isRegularFile(executable)) {
                            return executable;
                        }
                    }
                }
            }
        }

        Path commonLocation = Paths.get(
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe");

        if (Files.isRegularFile(commonLocation)) {
            return commonLocation;
        }

        return null;
    }

    private static void importBackup(
            Path mysqlExecutable,
            String username,
            String password) throws Exception {

        Path optionsFile = Files.createTempFile(
                "anura-mysql-", ".cnf");

        try {
            String options = "[client]\n"
                    + "user=\"" + escapeOption(username) + "\"\n"
                    + "password=\"" + escapeOption(password) + "\"\n"
                    + "host=localhost\n"
                    + "port=3306\n";

            Files.write(
                    optionsFile,
                    options.getBytes(StandardCharsets.UTF_8));

            ProcessBuilder builder = new ProcessBuilder(
                    mysqlExecutable.toString(),
                    "--defaults-extra-file=" + optionsFile.toAbsolutePath());

            builder.redirectInput(BACKUP_FILE.toFile());
            builder.redirectErrorStream(true);

            Process process = builder.start();


            String output;

            try (java.io.InputStream input = process.getInputStream();
            		java.io.ByteArrayOutputStream outputStream =
             new java.io.ByteArrayOutputStream()) {

            	byte[] buffer = new byte[4096];
            	int bytesRead;

            	while ((bytesRead = input.read(buffer)) != -1) {
            		outputStream.write(buffer, 0, bytesRead);
            	}

            	output = new String(
            			outputStream.toByteArray(),
            			StandardCharsets.UTF_8);
            }


            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException(
                        "MySQL could not import the backup.\n" + output);
            }

        } finally {
            Files.deleteIfExists(optionsFile);
        }
    }

    private static String generatePassword() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);

        StringBuilder password = new StringBuilder();

        for (byte value : bytes) {
            password.append(String.format("%02x", value & 0xff));
        }

        return password.toString();
    }

    private static String escapeSql(String value) {
        return value.replace("\\", "\\\\").replace("'", "''");
    }

    private static String escapeOption(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }


}
