import {Component, computed, inject, signal} from '@angular/core'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatChipsModule} from '@angular/material/chips'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PortalWorkflowService, PortalParticipantInfo} from '@board-buddy/portal'
import {TECHNOLOGY_TIER, TechnologyType, WorkflowResearchRequest} from '@board-buddy/core'

const TIERS = [1, 2, 3, 4, 5]
const ALL_TECHS = Object.keys(TECHNOLOGY_TIER) as TechnologyType[]

@Component({
  selector: 'portal-session-research-dialog',
  imports: [MatDialogModule, MatButtonModule, MatIconModule, MatTooltipModule, MatChipsModule, TranslatePipe],
  templateUrl: './session-research-dialog.component.html',
})
export class SessionResearchDialogComponent {
  private workflowService = inject(PortalWorkflowService)
  private dialogRef = inject(MatDialogRef<SessionResearchDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, participantInfo: PortalParticipantInfo} = inject(MAT_DIALOG_DATA)

  readonly tiers = TIERS

  private discovered = computed(() => new Set(this.data.participantInfo.technologies))

  readonly techsByTier = computed(() =>
    TIERS.reduce((acc, tier) => {
      acc[tier] = ALL_TECHS.filter(t => TECHNOLOGY_TIER[t] === tier)
      return acc
    }, {} as Record<number, TechnologyType[]>)
  )

  readonly researchable = signal<Set<TechnologyType>>(new Set())

  constructor() {
    const disc = this.discovered()
    const researchable = new Set<TechnologyType>()
    for (const tech of ALL_TECHS) {
      if (!disc.has(tech) && this.canResearch(tech, disc)) researchable.add(tech)
    }
    this.researchable.set(researchable)
  }

  private canResearch(tech: TechnologyType, discovered: Set<TechnologyType>): boolean {
    const tier = TECHNOLOGY_TIER[tech]
    if (tier === 1) return true
    const prevTierCount = ALL_TECHS.filter(t => TECHNOLOGY_TIER[t] === tier - 1 && discovered.has(t)).length
    const sameTierCount = ALL_TECHS.filter(t => TECHNOLOGY_TIER[t] === tier && discovered.has(t)).length
    return sameTierCount < prevTierCount - 1
  }

  isDiscovered(tech: TechnologyType): boolean { return this.discovered().has(tech) }
  isResearchable(tech: TechnologyType): boolean { return this.researchable().has(tech) }

  research(tech: TechnologyType) {
    const request = new WorkflowResearchRequest(this.data.participantInfo.player.player.id, tech)
    this.workflowService.research(this.data.sessionKey, request).subscribe({
      next: () => {
        this.translate.get('session.message.technologyResearched').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
