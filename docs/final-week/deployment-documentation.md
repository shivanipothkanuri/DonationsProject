# Deployment Documentation

## Target

- Railway (primary)
- EC2 (optional fallback)

## Required Environment Variables

- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- JWT_SECRET
- BREVO_SMTP_USERNAME
- BREVO_SMTP_KEY
- BREVO_API_KEY
- MAIL_FROM
- APP_BASE_URL

## Railway Steps

1. Connect repository to Railway.
2. Set all environment variables.
3. Ensure MySQL service or external MySQL URL is configured.
4. Deploy with Maven build command.
5. Verify health endpoint and login flow.

## Validation

- Public HTTPS URL accessible
- Database connectivity successful
- Email fallback path tested
- Key role flows pass smoke tests
