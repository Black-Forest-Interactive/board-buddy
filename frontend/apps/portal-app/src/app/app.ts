import {ChangeDetectionStrategy, Component, computed, effect, inject, resource, signal} from '@angular/core'
import {NavigationEnd, Router} from '@angular/router'
import {toSignal} from '@angular/core/rxjs-interop'
import {filter, map} from 'rxjs'
import {ShellComponent, ShellMenuGroup, ShellMenuItem, ShellService} from '@board-buddy/ui'
import {PlayerService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

const SESSION_KEY_STORAGE = 'portal-session-key'

@Component({
  imports: [ShellComponent],
  selector: 'portal-root',
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  private router = inject(Router)
  private shellService = inject(ShellService)
  private playerService = inject(PlayerService)

  private playerId = computed(() => this.playerService.getPlayerId())
  private playerResource = resource({
    params: this.playerId,
    loader: (p) => p.params ? toPromise(this.playerService.getPlayer(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private currentUrl = toSignal(
    this.router.events.pipe(filter(e => e instanceof NavigationEnd), map(e => (e as NavigationEnd).urlAfterRedirects)),
    {initialValue: this.router.url}
  )

  private urlSessionKey = computed(() => {
    const m = this.currentUrl().match(/^\/session\/([^/?#/]+)/)
    if (!m) return null
    return (m[1] === 'new' || m[1] === 'join') ? null : m[1]
  })

  private storedSessionKey = signal<string | null>(localStorage.getItem(SESSION_KEY_STORAGE))

  private sessionKey = computed(() => this.urlSessionKey() ?? this.storedSessionKey())

  constructor() {
    effect(() => {
      const key = this.urlSessionKey()
      if (key) {
        localStorage.setItem(SESSION_KEY_STORAGE, key)
        this.storedSessionKey.set(key)
      }
    })
    effect(() => {
      const name = this.playerResource.value()?.name ?? ''
      this.shellService.setPlayerName(name)
    })
  }

  readonly menuGroups = computed<ShellMenuGroup[]>(() => {
    const key = this.sessionKey()
    const groups: ShellMenuGroup[] = [{
      title: 'MENU.group.portal',
      items: [
        {routerLink: '/home', icon: 'home', text: 'MENU.Home', exact: true},
        {routerLink: '/player', icon: 'person', text: 'MENU.Player', exact: true},
      ]
    }]
    if (key) groups.push({
      title: 'MENU.group.session',
      items: [
        {routerLink: `/session/${key}`, icon: 'casino', text: 'MENU.Session', exact: true},
      ]
    })
    return groups
  })

  readonly bottomNavItems = computed<ShellMenuItem[]>(() => {
    const key = this.sessionKey()
    const items: ShellMenuItem[] = [
      {routerLink: '/home', icon: 'home', text: 'MENU.Home', exact: true},
    ]
    if (key) {
      items.push(
        {routerLink: `/session/${key}`, icon: 'casino', text: 'MENU.Session', exact: true},
        {routerLink: `/session/${key}/army`, icon: 'military_tech', text: 'MENU.Army', exact: true},
        {routerLink: `/session/${key}/research`, icon: 'science', text: 'MENU.Research', exact: true},
      )
    } else {
      items.push({routerLink: '/player', icon: 'person', text: 'MENU.Player', exact: true})
    }
    return items
  })
}
