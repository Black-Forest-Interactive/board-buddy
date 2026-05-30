import {inject, Injectable} from '@angular/core'
import {driver, DriveStep} from 'driver.js'
import {TranslateService} from '@ngx-translate/core'

@Injectable({providedIn: 'root'})
export class TourService {
  private translate = inject(TranslateService)

  private t(key: string): string {
    return this.translate.instant(key)
  }

  startHomeTour() {
    driver({
      showProgress: true,
      steps: [
        {element: '#tour-home-create', popover: {title: this.t('tour.home.create.title'), description: this.t('tour.home.create.description'), side: 'bottom', align: 'start'}},
        {element: '#tour-home-join', popover: {title: this.t('tour.home.join.title'), description: this.t('tour.home.join.description'), side: 'bottom', align: 'start'}},
        {element: '#tour-home-sessions', popover: {title: this.t('tour.home.sessions.title'), description: this.t('tour.home.sessions.description'), side: 'top', align: 'start'}},
      ]
    }).drive()
  }

  startLobbyTour() {
    const allSteps: DriveStep[] = [
      {element: '#tour-lobby-participants', popover: {title: this.t('tour.lobby.participants.title'), description: this.t('tour.lobby.participants.description'), side: 'top', align: 'start'}},
      {element: '#tour-lobby-army', popover: {title: this.t('tour.lobby.army.title'), description: this.t('tour.lobby.army.description'), side: 'top', align: 'start'}},
      {element: '#tour-lobby-research', popover: {title: this.t('tour.lobby.research.title'), description: this.t('tour.lobby.research.description'), side: 'top', align: 'start'}},
      {element: '.tour-lobby-battle', popover: {title: this.t('tour.lobby.battle.title'), description: this.t('tour.lobby.battle.description'), side: 'top', align: 'start'}},
      {element: '#tour-lobby-share', popover: {title: this.t('tour.lobby.share.title'), description: this.t('tour.lobby.share.description'), side: 'bottom', align: 'end'}},
    ]
    const steps = allSteps.filter(s => typeof s.element === 'string' && !!document.querySelector(s.element))
    driver({showProgress: true, steps}).drive()
  }

  startArmyTour() {
    driver({
      showProgress: true,
      steps: [
        {element: '#tour-army-grid', popover: {title: this.t('tour.army.grid.title'), description: this.t('tour.army.grid.description'), side: 'top', align: 'start'}},
      ]
    }).drive()
  }

  startResearchTour() {
    driver({
      showProgress: true,
      steps: [
        {element: '#tour-research-tiers', popover: {title: this.t('tour.research.tiers.title'), description: this.t('tour.research.tiers.description'), side: 'bottom', align: 'start'}},
        {element: '#tour-research-toggle', popover: {title: this.t('tour.research.toggle.title'), description: this.t('tour.research.toggle.description'), side: 'bottom', align: 'end'}},
        {element: '#tier-1', popover: {title: this.t('tour.research.grid.title'), description: this.t('tour.research.grid.description'), side: 'top', align: 'start'}},
      ]
    }).drive()
  }

  startBattleTour() {
    driver({
      showProgress: true,
      steps: [
        {element: '#tour-battle-banner', popover: {title: this.t('tour.battle.banner.title'), description: this.t('tour.battle.banner.description'), side: 'bottom', align: 'start'}},
        {element: '#tour-battle-fronts', popover: {title: this.t('tour.battle.fronts.title'), description: this.t('tour.battle.fronts.description'), side: 'top', align: 'start'}},
        {element: '#tour-battle-reserve', popover: {title: this.t('tour.battle.reserve.title'), description: this.t('tour.battle.reserve.description'), side: 'top', align: 'start'}},
      ]
    }).drive()
  }
}
