import {Injectable, Signal} from "@angular/core";
import {map} from "rxjs";
import {TranslateService} from "@ngx-translate/core";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {toSignal} from "@angular/core/rxjs-interop";

@Injectable({
  providedIn: 'root'
})
export class AppService {

  languages = [
    {key: 'en', text: 'LANG.English'},
    {key: 'de', text: 'LANG.German'},
  ]

  lang: Signal<string>
  isHandset: Signal<boolean>

  private readonly STORAGE_KEY_LANG = 'userLang'
  private readonly DEFAULT_LANG = 'en'

  constructor(
    private translate: TranslateService,
    private breakpointObserver: BreakpointObserver,
  ) {
    translate.setFallbackLang(this.DEFAULT_LANG)


    translate.use(this.getDefaultLang())


    this.lang = toSignal(
      translate.onLangChange.pipe(map(event => event.lang)),
      {initialValue: translate.getCurrentLang()}
    )

    this.isHandset = toSignal(
      this.breakpointObserver.observe(Breakpoints.Handset).pipe(
        map(result => result.matches),
      ),
      {initialValue: false}
    )

  }

  setLanguage(language: string) {
    if (this.isSupported(language)) {
      this.translate.use(language)
      localStorage.setItem(this.STORAGE_KEY_LANG, language)
    }
  }

  private getDefaultLang(): string {
    const savedLang = localStorage.getItem(this.STORAGE_KEY_LANG)
    if (savedLang && this.isSupported(savedLang)) return savedLang

    const browserLang = this.translate.getBrowserLang()
    if (browserLang && this.isSupported(browserLang)) return browserLang

    return this.DEFAULT_LANG
  }

  private isSupported(lang: string) {
    return this.languages.some(l => l.key === lang)
  }
}
