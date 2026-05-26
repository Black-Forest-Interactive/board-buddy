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
import {PortalWorkflowService} from '@board-buddy/portal'
import {toPromise} from '@board-buddy/shared'

const TIERS = [1, 2, 3, 4, 5]

@Component({
  selector: 'portal-session-research',
  imports: [MatButtonModule, MatIconModule, MatCardModule, MatChipsModule, MatTooltipModule, TranslatePipe],
  templateUrl: './session-research.component.html',
})
export class SessionResearchComponent {
  private workflowService = inject(PortalWorkflowService)
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

  readonly tiers = TIERS
  readonly techsByTier = computed(() => TIERS.reduce((acc, tier) => {
    acc[tier] = this.allTechs().filter(t => t.tier === tier)
    return acc
  }, {} as Record<number, Technology[]>))

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
