import { Component, TemplateRef } from '@angular/core';
import {AppToastService} from '../../services/app-toast.service';

@Component({
    selector: 'app-toast-container',
    template: `
		<ngb-toast
			*ngFor="let toast of toastService.toasts"
			[class]="toast.classname"
			[autohide]="true"
			[delay]="toast.delay || 50000"
			(hidden)="toastService.remove(toast)">
            <ng-template [ngIf]="isTemplate(toast)" [ngIfElse]="text">
                <ng-template [ngTemplateOutlet]="toast.textOrTpl"></ng-template>
            </ng-template>

			<ng-template #text>{{ toast.textOrTpl }}</ng-template>
		</ngb-toast>
	`,
    host: {'[class.ngb-toasts]': 'true'}
})
export class ToastContainerComponent {
    constructor(public toastService: AppToastService) {}

    isTemplate(toast: any): boolean {
        return toast.textOrTpl instanceof TemplateRef;
    }
}
