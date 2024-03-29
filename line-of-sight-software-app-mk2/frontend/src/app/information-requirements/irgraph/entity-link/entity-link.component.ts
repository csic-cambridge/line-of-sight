import {
    AfterViewInit,
    Component,
    Inject,
    Input,
    OnInit,
    OnDestroy,
    OnChanges,
    SimpleChanges, HostListener, ViewChild, ElementRef,
} from '@angular/core';
import {DOCUMENT} from '@angular/common';
import 'leader-line';
import {IRGraphComponent} from '../irgraph.component';

declare let LeaderLine: any;

@Component({
    selector: 'app-entity-link',
    templateUrl: './entity-link.component.html',
    styleUrls: ['./entity-link.component.scss']
})
export class EntityLinkComponent implements OnInit, AfterViewInit, OnDestroy {

    @Input() leftLink?: string;
    @Input() rightLink?: string;
    @Input() showIRs?: string;
    @Input() showSelected?: boolean;
    private resizeObserver: ResizeObserver = new ResizeObserver(this.onResize);
    private leaderline: any;

    constructor(@Inject(DOCUMENT) private document: any) {
    }

    ngOnDestroy(): void {
        if (this.leaderline !== undefined) {
            this.leaderline.remove();
        }
        const element = document.getElementById('leader-line-container');
        if (element !== null) {
            this.resizeObserver.unobserve(element);
            this.resizeObserver.disconnect();
        }

    }

    ngOnInit(): void {

    }

    ngAfterViewInit(): void {
        const element = document.getElementById('leader-line-container');
        if (element !== null) {
            this.resizeObserver.observe(element);
        }
        if (!this.createLink()) {
            // not sure why, but sometimes rightlink is not found (it should have rendered by now) but a second attempt will solve it.
            this.createLink();
        }

    }



    createLink(): boolean{
        if (this.leaderline === undefined && this.leftLink !== undefined && this.rightLink !== undefined) {
            const startElement = document.getElementById(this.leftLink);
            const endElement = document.getElementById(this.rightLink);

            if (startElement != null
                && endElement != null && (!this.showSelected || (endElement.classList.contains('selected')
                    && startElement.classList.contains('selected')))) {
                this.leaderline = new LeaderLine(startElement, endElement,
                    {
                        size: 2,
                        color: (startElement.classList.contains('selected') && endElement.classList.contains('selected')) ?
                            IRGraphComponent.darkblue : '#ccc',
                        startSocket: 'right',
                        endSocket: 'left',
                        zIndex: -1,
                    }
                );
                return true;
            }
        }
        return false;
    }

    onResize(): void {
        if (this.leaderline != null) {
            this.leaderline.position();
        }
    }

    setColor(color: string): void {
        if (this.leaderline != null) {
            this.leaderline.color = color
        }
    }

}
