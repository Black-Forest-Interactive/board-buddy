import {Component, computed, inject, resource, signal} from '@angular/core'
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute, Router} from '@angular/router'
import {catchError, EMPTY, map} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {MatChipsModule} from '@angular/material/chips'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {Technology, WorkflowResearchRequest} from '@board-buddy/core'
import {MainContentComponent} from '@board-buddy/ui'
import {PortalWorkflowService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

const TIERS = [1, 2, 3, 4, 5]

@Component({
  selector: 'portal-session-research',
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatChipsModule, MatTooltipModule, TranslatePipe, MainContentComponent],
  templateUrl: './session-research.component.html',
})
export class SessionResearchComponent {
  private workflowService = inject(PortalWorkflowService)
  private router = inject(Router)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private sessionKey = toSignal(this.route.paramMap.pipe(map(p => p.get('key') ?? '')))

  private myInfoResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getMyInfo(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private technologyStatusResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getTechnologyStatus(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private myInfo = computed(() => this.myInfoResource.value())
  private technologyStatus = computed(() => this.technologyStatusResource.value())

  private allTechs = computed(() => {
    const status = this.technologyStatus()
    if (!status) return []
    return [...status.researched, ...status.available, ...status.blocked]
  })

  private researchedIds = computed(() => new Set((this.technologyStatus()?.researched ?? []).map(t => t.id)))
  private availableIds = computed(() => new Set((this.technologyStatus()?.available ?? []).map(t => t.id)))

  readonly hideDiscovered = signal(false)

  readonly tiers = TIERS
  readonly techsByTier = computed(() => TIERS.reduce((acc, tier) => {
    acc[tier] = this.allTechs().filter(t => t.tier === tier)
    return acc
  }, {} as Record<number, Technology[]>))
  readonly techsByTierVisible = computed(() => {
    const hide = this.hideDiscovered()
    return TIERS.reduce((acc, tier) => {
      acc[tier] = this.techsByTier()[tier].filter(t => !hide || !this.isDiscovered(t))
      return acc
    }, {} as Record<number, Technology[]>)
  })

  constructor() {
    const key = this.sessionKey()
    if (key) {
      this.workflowService.getSessionEvents(key).pipe(
        catchError(() => EMPTY),
        takeUntilDestroyed(),
      ).subscribe(e => { if (e.type === 'BATTLE_STARTED') this.router.navigate(['/session', key]) })
    }
  }

  back() { this.router.navigate(['/session', this.sessionKey()]) }
  reload() { this.myInfoResource.reload(); this.technologyStatusResource.reload() }
  scrollToTier(tier: number) { document.getElementById('tier-' + tier)?.scrollIntoView({behavior: 'smooth', block: 'start'}) }

  isDiscovered(tech: Technology): boolean { return this.researchedIds().has(tech.id) }
  isResearchable(tech: Technology): boolean { return this.availableIds().has(tech.id) }

  research(tech: Technology) {
    const key = this.sessionKey()
    const info = this.myInfo()
    if (!key || !info) return
    this.workflowService.research(key, new WorkflowResearchRequest(info.player.id, tech.id)).subscribe({
      next: () => {
        this.translate.get('session.message.technologyResearched').subscribe(t => this.toast.success(t))
        this.myInfoResource.reload()
        this.technologyStatusResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
