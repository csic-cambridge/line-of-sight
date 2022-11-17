import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor, HttpErrorResponse, HttpStatusCode
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Router} from '@angular/router';

@Injectable()
export class HttpAuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const req = request.clone({withCredentials: true});
    return next.handle(req).pipe(map((event: HttpEvent<any>) => {
            return event;
        }),
        catchError((httpErrorResponse: HttpErrorResponse, event) => {
            if (httpErrorResponse.status === HttpStatusCode.Unauthorized) {
                this.router.navigate(['/login']);
            }
            else if (httpErrorResponse.status === HttpStatusCode.Forbidden) {
                window.alert('You do not have permission to perform this function');
            }
            return throwError(httpErrorResponse);
        })
    );
  }
}
