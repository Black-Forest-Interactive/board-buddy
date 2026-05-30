// Platzhalter-Version für das öffentliche Repository.
// Die echte Konfiguration mit persönlichen Daten liegt im privaten Deployment-Repo
// und wird von der CI/CD-Pipeline vor dem Build eingespielt.
export const LEGAL = {
  name:                 '[Name]',
  street:               '[Straße und Hausnummer]',
  city:                 '[PLZ Ort]',
  email:                '[E-Mail]',
  license:              'MIT',
  githubUrl:            'https://github.com/Black-Forest-Interactive/board-buddy',
  logRetentionDays:     '[X]',
  playerDataRetention:  '[Beschreibung]',
  legalDate:            '[Monat Jahr]',
} as const
