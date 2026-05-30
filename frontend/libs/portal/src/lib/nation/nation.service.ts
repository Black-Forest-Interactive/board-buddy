import {Injectable} from '@angular/core'
import {Observable} from 'rxjs'
import {BaseService} from '@board-buddy/shared'
import {Nation} from '@board-buddy/core'

@Injectable({providedIn: 'root'})
export class PortalNationService extends BaseService {
  constructor() { super('portal/nation') }

  getNations(): Observable<Nation[]> {
    return this.getAll<Nation>('')
  }
}
