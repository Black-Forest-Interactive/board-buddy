import {Component, computed, inject, resource} from '@angular/core'
import {toSignal} from '@angular/core/rxjs-interop'
import {ActivatedRoute} from '@angular/router'
import {map} from 'rxjs'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatCardModule} from '@angular/material/card'
import {MatChipsModule} from '@angular/material/chips'
import {MatTooltipModule} from '@angular/material/tooltip'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {TECHNOLOGY_TIER, TechnologyType, WorkflowResearchRequest} from '@board-buddy/core'
import {PlayerService, PortalWorkflowService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

const TIERS = [1, 2, 3, 4, 5]
const ALL_TECHS = Object.keys(TECHNOLOGY_TIER) as TechnologyType[]

@Component({
  selector: 'portal-session-research',
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatChipsModule, MatTooltipModule, TranslatePipe],
  templateUrl: './session-research.component.html',
})
export class SessionResearchComponent {
  private workflowService = inject(PortalWorkflowService)
  private playerService = inject(PlayerService)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)
  private route = inject(ActivatedRoute)

  private sessionKey = toSignal(this.route.paramMap.pipe(map(p => p.get('key') ?? '')))
  private playerId = computed(() => this.playerService.getPlayerId())

  private myInfoResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getMyInfo(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private myInfo = computed(() => this.myInfoResource.value())
  readonly myTechnologies = computed(() => this.myInfo()?.technologies ?? [])
  private discovered = computed(() => new Set(this.myTechnologies()))

  readonly tiers = TIERS
  readonly techsByTier = computed(() => TIERS.reduce((acc, tier) => {
    acc[tier] = ALL_TECHS.filter(t => TECHNOLOGY_TIER[t] === tier)
    return acc
  }, {} as Record<number, TechnologyType[]>))

  readonly researchable = computed(() => {
    const disc = this.discovered()
    const result = new Set<TechnologyType>()
    for (const tech of ALL_TECHS) {
      if (!disc.has(tech) && this.canResearch(tech, disc)) result.add(tech)
    }
    return result
  })

  private canResearch(tech: TechnologyType, disc: Set<TechnologyType>): boolean {
    const tier = TECHNOLOGY_TIER[tech]
    if (tier === 1) return true
    const prevCount = ALL_TECHS.filter(t => TECHNOLOGY_TIER[t] === tier - 1 && disc.has(t)).length
    const sameCount = ALL_TECHS.filter(t => TECHNOLOGY_TIER[t] === tier && disc.has(t)).length
    return sameCount < prevCount - 1
  }

  isDiscovered(tech: TechnologyType): boolean { return this.discovered().has(tech) }
  isResearchable(tech: TechnologyType): boolean { return this.researchable().has(tech) }

  research(tech: TechnologyType) {
    const key = this.sessionKey()
    const info = this.myInfo()
    if (!key || !info) return
    this.workflowService.research(key, new WorkflowResearchRequest(info.player.player.id, tech)).subscribe({
      next: () => {
        this.translate.get('session.message.technologyResearched').subscribe(t => this.toast.success(t))
        this.myInfoResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
