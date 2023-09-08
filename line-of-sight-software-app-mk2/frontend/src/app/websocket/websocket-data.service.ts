import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { environment } from '../../environments/environment';
import { catchError, tap, switchAll } from 'rxjs/operators';
import { EMPTY, Subject, Observable, ObservableInput, timer } from 'rxjs';
import { delayWhen, retryWhen } from 'rxjs/operators';

//export const WS_ENDPOINT = environment.wsEndpoint;

@Injectable({
  providedIn: 'root'
})
export class WebsocketDataService {

    private socket$: WebSocketSubject<any> | undefined;
    private messagesSubject$ = new Subject<Observable<any>>();
    public messages$ = this.messagesSubject$.pipe(switchAll(), catchError(e => { throw e }));

    public connect(cfg: { reconnect: boolean } = { reconnect: false }): void {
        if (!this.socket$ || this.socket$.closed) {
          console.log("about to open websocket")
          this.socket$ = this.getNewWebSocket();
          const messages = this.socket$.pipe(cfg.reconnect ? this.reconnect : o => o,
            tap({
              next: x => console.log("Websocket next: ", x),
              error: error => console.log("websocket error:", error),
            }), catchError(_ => EMPTY))
          this.messagesSubject$.next(messages);
        }
      }

    private getNewWebSocket() {
        let wsProtocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
        return webSocket({
            url: wsProtocol + '://' + window.location.host + '/cdbb-ws',
            closeObserver: {
                next: () => {
                    console.log('[Websocket]: connection closed');
                    this.socket$ = undefined;
                    this.connect({ reconnect: true });
                }
            },
        });
    }

    sendMessage(msg: any) {
        if (this.socket$ != undefined) {
            this.socket$.next(msg);
        }
    }

    private reconnect(observable: Observable<any>): Observable<any> {
        return observable.pipe(retryWhen(errors => errors.pipe(tap(val => console.log('[Websocket] Try to reconnect', val)),
          delayWhen(_ => timer(5000)))));
    }

    close() {
        if (this.socket$ != undefined) {
            this.socket$.complete();
        }
    }

}
