# Authentication Package

A comprehensive, secure, and production-ready authentication system built with Spring Boot and Java. This package provides a complete authentication solution that can be easily integrated into any Spring Boot application.

## Features

- 🔐 **JWT-based Authentication** - Secure token-based authentication with access and refresh tokens
- 👤 **User Management** - Complete user registration, login, and profile management
- 🔑 **Password Security** - BCrypt password hashing with configurable strength
- 📧 **Email Verification** - Optional email verification for new user accounts
- 🔄 **Password Reset** - Secure password reset flow with email notifications
- 🛡️ **Role-Based Access Control (RBAC)** - Flexible role and permission system
- 🚦 **Rate Limiting** - Built-in rate limiting to prevent abuse
- 🔒 **Security Features** - CSRF protection, CORS configuration, account locking
- 📝 **Comprehensive Exception Handling** - Detailed error messages and proper HTTP status codes
- ⚙️ **Highly Configurable** - Extensive configuration options via properties files

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Spring Boot 3.2.0+
- Database (H2 for testing, PostgreSQL/MySQL for production)
- SMTP server for email functionality (optional)

## Quick Start

### Step 1: Install the Package

Choose one of the installation methods from the [Installation](#installation) section above.

### Step 2: Add to Your Project

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.authpackage</groupId>
    <artifactId>authentication-package</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Step 3: Configure Database

For quick testing with H2 (in-memory database), add to `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:authdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update
```

### Step 4: Configure JWT Secret

Add to `application.yml` or set as environment variable:

```yaml
auth:
  jwt:
    secret: ${JWT_SECRET:change-this-to-a-secure-random-256-bit-key}
```

**Important:** Generate a secure secret for production:
```bash
# Generate a secure 256-bit key
openssl rand -base64 32
```

### Step 5: Run Your Application

```bash
mvn spring-boot:run
```

### Step 6: Test Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "Test123456!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Step 7: Test Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "test@example.com",
    "password": "Test123456!"
  }'
```

You should receive an access token and refresh token in the response. Use the access token in the `Authorization` header for protected endpoints:

```bash
curl -X GET http://localhost:8080/api/protected \
  -H "Authorization: Bearer {your-access-token}"
```

### Optional: Configure Email (for email verification and password reset)

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

auth:
  email:
    from: noreply@yourdomain.com
    verification-url: http://localhost:8080/api/auth/verify-email?token=
    reset-password-url: http://localhost:8080/api/auth/reset-password?token=
```

**Note:** If email verification is enabled but email is not configured, users will be created but unable to login until email is verified. You can disable email verification for testing:

```yaml
auth:
  email-verification:
    enabled: false
```

## Installation

### Option 1: Install from GitHub Packages (Recommended)

If published to GitHub Packages, add the repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/YOUR_USERNAME/Authentication-Package</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.authpackage</groupId>
        <artifactId>authentication-package</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**Note:** You'll need to authenticate with GitHub Packages. Create a Personal Access Token with `read:packages` permission and add it to your `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>YOUR_USERNAME</username>
            <password>YOUR_GITHUB_TOKEN</password>
        </server>
    </servers>
</settings>
```

### Option 2: Install Locally from Source

Clone the repository and install to your local Maven repository:

```bash
git clone https://github.com/YOUR_USERNAME/Authentication-Package.git
cd Authentication-Package
mvn clean install
```

Then add the dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.authpackage</groupId>
    <artifactId>authentication-package</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 3: Install from Maven Central (Future)

Once published to Maven Central, simply add:

```xml
<dependency>
    <groupId>com.authpackage</groupId>
    <artifactId>authentication-package</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Configuration

Add the following to your `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/yourdb
    username: yourusername
    password: yourpassword
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

auth:
  jwt:
    secret: ${JWT_SECRET:your-256-bit-secret-key-change-this-in-production}
    expiration: 86400000  # 24 hours in milliseconds
    refresh-expiration: 604800000  # 7 days in milliseconds
  
  email:
    from: noreply@yourdomain.com
    verification-url: https://yourdomain.com/api/auth/verify-email?token=
    reset-password-url: https://yourdomain.com/api/auth/reset-password?token=
  
  security:
    public-endpoints: /api/auth/**,/public/**
  
  cors:
    allowed-origins: https://yourdomain.com,https://www.yourdomain.com
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: *
  
  email-verification:
    enabled: true
    expiry-hours: 24
  
  password-reset:
    expiry-hours: 1
  
  refresh-token:
    expiry-days: 7
  
  rate-limit:
    enabled: true
    requests-per-minute: 60
    login-requests-per-minute: 5
```

## API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "johndoe",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "abc123...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "username": "johndoe",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["ROLE_USER"],
    "emailVerified": false
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrUsername": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response:** Same as register response

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "abc123..."
}
```

**Response:** Same as register response with new tokens

#### Verify Email
```http
POST /api/auth/verify-email
Content-Type: application/json

{
  "token": "verification-token-from-email"
}
```

**Response:**
```json
{
  "message": "Email verified successfully",
  "success": true
}
```

#### Forgot Password
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "message": "If an account exists with this email, a password reset link has been sent",
  "success": true
}
```

#### Reset Password
```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "reset-token-from-email",
  "newPassword": "NewSecurePassword123!"
}
```

**Response:**
```json
{
  "message": "Password reset successfully",
  "success": true
}
```

#### Logout
```http
POST /api/auth/logout
Content-Type: application/json
Authorization: Bearer {accessToken}

{
  "refreshToken": "abc123..."
}
```

**Response:**
```json
{
  "message": "Logged out successfully",
  "success": true
}
```

### Using Authenticated Endpoints

Include the JWT token in the Authorization header:

```http
GET /api/protected-endpoint
Authorization: Bearer {accessToken}
```

## Security Features

### Password Requirements
- Minimum 8 characters
- Maximum 100 characters
- Stored using BCrypt with strength 12

### JWT Tokens
- Access tokens: Short-lived (default 24 hours)
- Refresh tokens: Long-lived (default 7 days)
- Tokens are stored securely and can be revoked

### Rate Limiting
- General endpoints: 60 requests per minute per IP
- Login/Register endpoints: 5 requests per minute per IP
- Configurable via properties

### Account Security
- Account locking support
- Account expiration support
- Credential expiration support
- Email verification requirement (configurable)

## Role-Based Access Control

The system comes with three default roles:

1. **ROLE_USER** - Default role for all registered users
2. **ROLE_MODERATOR** - Limited admin access (read permissions)
3. **ROLE_ADMIN** - Full administrative access

### Custom Roles and Permissions

You can create custom roles and permissions programmatically:

```java
@Autowired
private RoleRepository roleRepository;

@Autowired
private PermissionRepository permissionRepository;

public void createCustomRole() {
    Permission permission = Permission.builder()
        .name("CUSTOM_PERMISSION")
        .description("Custom permission")
        .build();
    permissionRepository.save(permission);
    
    Role role = Role.builder()
        .name("ROLE_CUSTOM")
        .description("Custom role")
        .build();
    role.addPermission(permission);
    roleRepository.save(role);
}
```

## Error Handling

The package includes comprehensive error handling with appropriate HTTP status codes:

- `400 Bad Request` - Invalid input or expired tokens
- `401 Unauthorized` - Invalid credentials or missing authentication
- `403 Forbidden` - Account disabled/locked or insufficient permissions
- `404 Not Found` - User not found
- `409 Conflict` - User already exists
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Unexpected server errors

## Database Schema

The package automatically creates the following tables:
- `users` - User accounts
- `roles` - User roles
- `permissions` - System permissions
- `user_roles` - User-role mapping
- `role_permissions` - Role-permission mapping
- `refresh_tokens` - Refresh token storage

## Environment Variables

Recommended environment variables for production:

```bash
JWT_SECRET=your-very-secure-256-bit-secret-key
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_FROM=noreply@yourdomain.com
EMAIL_VERIFICATION_URL=https://yourdomain.com/api/auth/verify-email?token=
EMAIL_RESET_URL=https://yourdomain.com/api/auth/reset-password?token=
CORS_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS=60
RATE_LIMIT_LOGIN=5
```

## Testing

Run the application:

```bash
mvn spring-boot:run
```

Or build and run:

```bash
mvn clean package
java -jar target/authentication-package-1.0.0.jar
```

## Example Usage

### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "Test123456!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "test@example.com",
    "password": "Test123456!"
  }'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/protected \
  -H "Authorization: Bearer {accessToken}"
```

## Publishing the Package

### Publishing to GitHub Packages

1. **Set up GitHub Actions** (optional, for automated publishing):
   - Create `.github/workflows/publish.yml` (see below)

2. **Manual Publishing**:
   ```bash
   export GITHUB_TOKEN=your_github_token
   export GITHUB_REPOSITORY_OWNER=your_username
   export GITHUB_REPOSITORY_NAME=Authentication-Package
   mvn clean deploy
   ```

3. **Configure Maven Settings** (`~/.m2/settings.xml`):
   ```xml
   <settings>
       <servers>
           <server>
               <id>github</id>
               <username>YOUR_USERNAME</username>
               <password>YOUR_GITHUB_TOKEN</password>
           </server>
       </servers>
   </settings>
   ```

### Publishing to Maven Central

For Maven Central publication, you'll need:
1. Sonatype account and project approval
2. GPG key for signing
3. Additional Maven plugins configuration

See [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-guide/) for detailed instructions.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License.

## Support

For issues, questions, or contributions, please open an issue on GitHub.

## Security Considerations

1. **Always change the JWT secret** in production
2. **Use HTTPS** in production environments
3. **Configure CORS** properly for your domain
4. **Use strong database passwords**
5. **Enable rate limiting** in production
6. **Keep dependencies updated**
7. **Use environment variables** for sensitive configuration
8. **Regularly rotate JWT secrets**

## Production Deployment Checklist

### Security
- ✅ No hardcoded secrets or passwords
- ✅ JWT secret configurable via environment variables
- ✅ BCrypt password hashing (strength 12)
- ✅ SQL injection prevention (using JPA repositories)
- ✅ CSRF protection configured
- ✅ CORS properly configured
- ✅ Rate limiting implemented
- ✅ Input validation on all endpoints
- ✅ Proper exception handling (no sensitive info leakage)
- ✅ Account locking and expiration support

### Code Quality
- ✅ No System.out.println or printStackTrace
- ✅ Proper null checks with Optional
- ✅ Transaction management (@Transactional)
- ✅ Proper exception hierarchy
- ✅ Comprehensive error handling
- ✅ Input validation with @Valid

### Configuration
- ✅ Environment variable support
- ✅ Configurable via application.yml
- ✅ Production-ready logging levels (WARN for security)
- ✅ Database configuration externalized
- ✅ Email configuration externalized

### Required Environment Variables for Production
```bash
JWT_SECRET=<generate-256-bit-key>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<your-password>
MAIL_FROM=noreply@yourdomain.com
EMAIL_VERIFICATION_URL=https://yourdomain.com/api/auth/verify-email?token=
EMAIL_RESET_URL=https://yourdomain.com/api/auth/reset-password?token=
CORS_ORIGINS=https://yourdomain.com
```

### Production Deployment Notes
1. **Database**: Replace H2 with PostgreSQL/MySQL for production
2. **Connection Pooling**: HikariCP is configured by default
3. **DDL Auto**: Set to `validate` or `none` for production
4. **H2 Console**: Disable in production (`spring.h2.console.enabled=false`)
5. **HTTPS**: Always use HTTPS in production
6. **CORS**: Configure specific origins, not wildcards
7. **Rate Limiting**: Keep enabled in production
8. **Logging**: Use WARN level for security frameworks in production

## Roadmap

- [ ] OAuth2 integration (Google, GitHub, etc.)
- [ ] Two-factor authentication (2FA)
- [ ] Social login support
- [ ] Account activity logging
- [ ] Password history and complexity requirements
- [ ] Session management dashboard
- [ ] Multi-tenancy support
