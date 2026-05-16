import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";
import {LoggingService} from "./logging/logging.service";
import {inject} from "@angular/core";
import {Page} from "./shared.api";


export abstract class BaseService {
  api = 'api/';
  private http = inject(HttpClient)
  private logger = inject(LoggingService)

  protected constructor(private urlPrefix: string) {
  }

  protected getAll<T>(suffix: string = ''): Observable<Array<T>> {
    const url = this.createUrl(suffix);
    console.debug("Get all '" + url + "'")
    const observable = this.http.get<T[]>(url);
    return this.createPipe(observable, url, 'getAll');
  }

  protected get<T>(suffix: string, params = new HttpParams()): Observable<T> {
    const url = this.createUrl(suffix);
    console.debug("Get '" + url + "'")
    const observable = this.http.get<T>(url, {params: params});
    return this.createPipe(observable, url, 'get');
  }

  protected getPaged<T>(suffix: string, page: number, size: number, params = new HttpParams(), queryParams: string = ''): Observable<Page<T>> {
    const url = this.createUrl(suffix);
    console.debug("Get paged '" + url + "'")
    const uri = url + "?page=" + page.toString() + "&size=" + size.toString() + queryParams
    const observable = this.http.get<Page<T>>(uri);
    return this.createPipe(observable, url, 'get');
  }


  protected postPaged<T>(suffix: string, body: any, page: number, size: number, params = new HttpParams()): Observable<Page<T>> {
    const url = this.createUrl(suffix);
    console.debug("Post paged '" + url + "'")
    const uri = url + "?page=" + page.toString() + "&size=" + size.toString();
    const observable = this.http.post<Page<T>>(uri, body);
    return this.createPipe(observable, url, 'post');
  }

  protected getBlob(suffix: string): Observable<HttpResponse<Blob>> {
    const url = this.createUrl(suffix);
    console.debug("Get blob '" + url + "'")
    const headers = new HttpHeaders().set("Accept", "application/octet-stream");

    // @ts-ignore
    return this.http.get<Blob>(url, {
      headers: headers,
      observe: 'response',
      // @ts-ignore
      responseType: 'blob'
    })
  }


  protected postBlob(suffix: string, body: any, params = new HttpParams()): Observable<HttpResponse<Blob>> {
    const url = this.createUrl(suffix);
    console.debug("Post blob '" + url + "'")
    const headers = new HttpHeaders().set("Accept", "application/octet-stream")

    // @ts-ignore
    return this.http.post<Blob>(url, body, {
      headers: headers,
      observe: 'response',
      params: params,
      // @ts-ignore
      responseType: 'blob'
    })
  }

  protected put<T>(suffix: string, body: any, params: HttpParams = new HttpParams()): Observable<T> {
    this.logger.info("[PUT] " + JSON.stringify(body));
    const url = this.createUrl(suffix);
    console.debug("Put '" + url + "'")
    const observable = this.http.put<T>(url, body, {params: params});
    return this.createPipe(observable, url, 'put');
  }

  protected post<T>(suffix: string, body: any, params: HttpParams = new HttpParams()): Observable<T> {
    this.logger.info("[POST] " + JSON.stringify(body));
    const url = this.createUrl(suffix);
    console.debug("Post '" + url + "'")
    const observable = this.http.post<T>(url, body, {params: params})
    return this.createPipe(observable, url, 'post');
  }

  protected patch<T>(suffix: string, body: any): Observable<T> {
    this.logger.info("[PATCH] " + JSON.stringify(body));
    const url = this.createUrl(suffix);
    console.debug("Patch '" + url + "'")
    const observable = this.http.patch<T>(url, body);
    return this.createPipe(observable, url, 'patch');
  }

  protected delete<T>(suffix: string, params: HttpParams = new HttpParams()): Observable<T> {
    const url = this.createUrl(suffix);
    console.debug("Delete '" + url + "'")
    const observable = this.http.delete<T>(url, {params: params})
    return this.createPipe(observable, url, 'delete');
  }

  private createPipe<T>(observable: Observable<T>, url: string, operation: string): Observable<T> {
    const message = operation + ': ' + url;
    const log = this.logger;
    // @ts-ignore
    return observable.pipe(
      tap(
        function (next) {
          log.debug(message, next)
        },
        function (err) {
          log.error(message, err)
        },
        function () {
          log.info(message)
        }
      )
    );
  }


  protected stream(suffix: string): EventSource {
    const url = this.createUrl(suffix);
    console.debug("Stream '" + url + "'")
    return new EventSource(url, {withCredentials: true})
  }


  protected createUrl(suffix: string): string {
    if (suffix.length === 0) {
      return (this.urlPrefix.length === 0) ? `${this.api}` : `${this.api}${this.urlPrefix}`
    } else {
      return (this.urlPrefix.length === 0) ? `${this.api}${suffix}` : `${this.api}${this.urlPrefix}/${suffix}`;
    }
  }

}
