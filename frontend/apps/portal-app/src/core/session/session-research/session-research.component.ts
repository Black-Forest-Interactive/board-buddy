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
import {Technology, WorkflowResearchRequest} from '@board-buddy/core'
import {PlayerService, PortalWorkflowService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

const TIERS = [1, 2, 3, 4, 5]

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

  private myInfoResource = resource({
    params: this.sessionKey,
    loader: (p) => p.params ? toPromise(this.workflowService.getMyInfo(p.params), p.abortSignal) : Promise.resolve(undefined)
  })

  private myInfo = computed(() => this.myInfoResource.value())
  private allTechs = computed(() => this.myInfo()?.availableTechnologies ?? [])
  private discoveredIds = computed(() => new Set((this.myInfo()?.technologies ?? []).map(t => t.id)))

  readonly tiers = TIERS
  readonly techsByTier = computed(() => TIERS.reduce((acc, tier) => {
    acc[tier] = this.allTechs().filter(t => t.tier === tier)
    return acc
  }, {} as Record<number, Technology[]>))

  readonly researchable = computed(() => {
    const disc = this.discoveredIds()
    const result = new Set<number>()
    for (const tech of this.allTechs()) {
      if (!disc.has(tech.id) && this.canResearch(tech, disc)) result.add(tech.id)
    }
    return result
  })

  private canResearch(tech: Technology, disc: Set<number>): boolean {
    if (tech.tier === 1) return true
    const prevCount = this.allTechs().filter(t => t.tier === tech.tier - 1 && disc.has(t.id)).length
    const sameCount = this.allTechs().filter(t => t.tier === tech.tier && disc.has(t.id)).length
    return sameCount < prevCount - 1
  }

  isDiscovered(tech: Technology): boolean { return this.discoveredIds().has(tech.id) }
  isResearchable(tech: Technology): boolean { return this.researchable().has(tech.id) }

  research(tech: Technology) {
    const key = this.sessionKey()
    const info = this.myInfo()
    if (!key || !info) return
    this.workflowService.research(key, new WorkflowResearchRequest(info.player.player.id, tech.id)).subscribe({
      next: () => {
        this.translate.get('session.message.technologyResearched').subscribe(t => this.toast.success(t))
        this.myInfoResource.reload()
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
