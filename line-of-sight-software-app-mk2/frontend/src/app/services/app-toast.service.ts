import { Injectable, TemplateRef } from '@angular/core';

export interface ToastInfo {
    header: string;
    body: string;
    classname: string;
    delay?: number;
}

@Injectable({ providedIn: 'root' })
export class AppToastService {
    toasts: any[] = [];

    show(textOrTpl: string | TemplateRef<any>, options: any = {}): void {
        this.toasts.push({ textOrTpl, ...options });
    }

    remove(toast: any): void {
        this.toasts = this.toasts.filter((t) => t !== toast);
    }

    clear(): void {
        this.toasts.splice(0, this.toasts.length);
    }
}
