import {ChangeDetectionStrategy, Component} from '@angular/core';

@Component({
  selector: 'shared-shared',
  imports: [],
  templateUrl: './shared.html',
  styleUrl: './shared.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Shared {}
