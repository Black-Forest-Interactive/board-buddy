import {ChangeDetectionStrategy, Component, computed, inject, resource} from '@angular/core'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {MatButtonModule} from '@angular/material/button'
import {MatIconModule} from '@angular/material/icon'
import {MatTooltipModule} from '@angular/material/tooltip'
import {MatChipsModule} from '@angular/material/chips'
import {TranslatePipe, TranslateService} from '@ngx-translate/core'
import {HotToastService} from '@ngxpert/hot-toast'
import {PortalWorkflowService, PortalParticipantInfo} from '@board-buddy/portal'
import {Technology, WorkflowResearchRequest} from '@board-buddy/core'
import {toPromise} from '@board-buddy/shared'

const TIERS = [1, 2, 3, 4, 5]

@Component({
  selector: 'portal-session-research-dialog',
  imports: [MatDialogModule, MatButtonModule, MatIconModule, MatTooltipModule, MatChipsModule, TranslatePipe],
  templateUrl: './session-research-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionResearchDialogComponent {
  private workflowService = inject(PortalWorkflowService)
  private dialogRef = inject(MatDialogRef<SessionResearchDialogComponent>)
  private toast = inject(HotToastService)
  private translate = inject(TranslateService)

  readonly data: {sessionKey: string, participantInfo: PortalParticipantInfo} = inject(MAT_DIALOG_DATA)

  private technologyStatusResource = resource({
    loader: () => toPromise(this.workflowService.getTechnologyStatus(this.data.sessionKey))
  })

  private technologyStatus = computed(() => this.technologyStatusResource.value())

  private allTechs = computed(() => {
    const status = this.technologyStatus()
    if (!status) return []
    return [...status.researched, ...status.available, ...status.blocked]
  })

  private researchedIds = computed(() => new Set((this.technologyStatus()?.researched ?? []).map(t => t.id)))
  private availableIds = computed(() => new Set((this.technologyStatus()?.available ?? []).map(t => t.id)))

  readonly tiers = TIERS
  readonly techsByTier = computed(() =>
    TIERS.reduce((acc, tier) => {
      acc[tier] = this.allTechs().filter(t => t.tier === tier)
      return acc
    }, {} as Record<number, Technology[]>)
  )

  isDiscovered(tech: Technology): boolean { return this.researchedIds().has(tech.id) }
  isResearchable(tech: Technology): boolean { return this.availableIds().has(tech.id) }

  research(tech: Technology) {
    const request = new WorkflowResearchRequest(this.data.participantInfo.player.id, tech.id)
    this.workflowService.research(this.data.sessionKey, request).subscribe({
      next: () => {
        this.translate.get('session.message.technologyResearched').subscribe(t => this.toast.success(t))
        this.dialogRef.close(true)
      },
      error: () => this.translate.get('session.message.error').subscribe(t => this.toast.error(t))
    })
  }
}
