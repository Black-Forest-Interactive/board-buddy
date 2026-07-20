import {ChangeDetectionStrategy, Component, inject} from '@angular/core'
import {MatButtonModule} from '@angular/material/button'
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog'
import {TranslatePipe} from '@ngx-translate/core'
import {SessionQrcodeComponent} from '../session-qrcode/session-qrcode.component'

@Component({
  selector: 'portal-session-qrcode-dialog',
  imports: [MatDialogModule, MatButtonModule, TranslatePipe, SessionQrcodeComponent],
  templateUrl: './session-qrcode-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionQrcodeDialogComponent {
  readonly dialogRef = inject(MatDialogRef<SessionQrcodeDialogComponent>)
  readonly data: {sessionId: string, qrUrl: string} = inject(MAT_DIALOG_DATA)
}
