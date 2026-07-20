import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
  selector: 'portal-portal',
  imports: [],
  templateUrl: './portal.html',
  styleUrl: './portal.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Portal {}
