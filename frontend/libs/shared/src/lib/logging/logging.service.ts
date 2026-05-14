import { Injectable } from '@angular/core'

@Injectable({ providedIn: 'root' })
export class LoggingService {
  info(message: string, ...args: unknown[]) { console.info(message, ...args) }
  debug(message: string, ...args: unknown[]) { console.debug(message, ...args) }
  error(message: string, ...args: unknown[]) { console.error(message, ...args) }
}
