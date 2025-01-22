# Fog Carport Gruppe C

Dette repository indholder vores Fog carport webapplikations projekt.

## Links
- [Vores rapport som PDF](https://github.com/mrPrimeBeef/FogCarport/blob/peter/Rapport.pdf)
- [Video-demo af vores web app](https://www.youtube.com/watch?v=kcoIhAZ_xQo)
- [Figma filer](https://github.com/mrPrimeBeef/FogCarport/tree/peter/Figma)

## Installationsvejledning
1. Klon dette repository
2. Opret en `fog` database i PostgreSQL
3. Åbn IntelliJ og importer alle dependencies
4. Kopiér SQL scriptet fra `src/main/resources/sql/Sql-script.sql` til PostgreSQL og kør det
5. Sørg for at din SendGrid er sat op til at sende de tre emails som denne web app sender
6. Konfigurer disse environment variables i IntelliJ:
   - FROM_EMAIL_ADDRESS
   - SENDGRID_API_KEY
   - TEMPLATE_ID_QUOTE_CONFIRMATION
   - TEMPLATE_ID_QUOTE_READY
   - TEMPLATE_ID_FORGOT_PASSWORD 
7. Byg og kør projektet i IntelliJ
8. Se web appen ved at åbne `localhost:7070` i din browser