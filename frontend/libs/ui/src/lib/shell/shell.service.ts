import { computed, inject, Injectable, signal } from '@angular/core'
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout'
import { TranslateService } from '@ngx-translate/core'
import { toSignal } from '@angular/core/rxjs-interop'
import { map } from 'rxjs'

@Injectable({ providedIn: 'root' })
export class ShellService {
  private breakpointObserver = inject(BreakpointObserver)
  private translateService = inject(TranslateService)

  readonly isHandset = toSignal(
    this.breakpointObserver.observe(Breakpoints.Handset).pipe(map(r => r.matches)),
    { initialValue: false }
  )

  readonly title = signal('')
  readonly playerName = signal('')
  readonly playerInitial = computed(() => this.playerName() ? this.playerName().charAt(0).toUpperCase() : '')

  readonly lang = toSignal(
    this.translateService.onLangChange.pipe(map(e => e.lang)),
    { initialValue: this.translateService.currentLang ?? 'en' }
  )

  setTitle(title: string) { this.title.set(title) }
  setPlayerName(name: string) { this.playerName.set(name) }
  setLanguage(lang: string) { this.translateService.use(lang) }
}
