import {ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners, provideZoneChangeDetection} from '@angular/core'
import {provideRouter, withComponentInputBinding} from '@angular/router'
import {appRoutes} from './app.routes'
import {provideLuxonDateAdapter} from '@angular/material-luxon-adapter'
import {MAT_DATE_LOCALE} from '@angular/material/core'
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from '@angular/material/form-field'
import {provideHttpClient, withInterceptors} from '@angular/common/http'
import {provideTranslateService} from '@ngx-translate/core'
import {provideTranslateHttpLoader} from '@ngx-translate/http-loader'
import {registerLocaleData} from '@angular/common'
import de from '@angular/common/locales/de'
import {provideAnimations} from '@angular/platform-browser/animations'
import {provideToastConfig} from '@board-buddy/ui'
import {provideKeycloakAngular} from "./keycloak.config"
import {includeBearerTokenInterceptor} from "keycloak-angular"

registerLocaleData(de)

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimations(),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideLuxonDateAdapter(),
    {provide: MAT_DATE_LOCALE, useValue: 'de-DE'},
    {provide: LOCALE_ID, useValue: 'de-DE'},
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},
    provideToastConfig(),
    provideKeycloakAngular(),
    provideHttpClient(withInterceptors([
      includeBearerTokenInterceptor
    ])),
    provideTranslateService({
      loader: provideTranslateHttpLoader({prefix: '/i18n/', suffix: '.json'}),
      fallbackLang: 'de',
      lang: 'en'
    }),
    provideRouter(appRoutes, withComponentInputBinding()),
  ],
}
