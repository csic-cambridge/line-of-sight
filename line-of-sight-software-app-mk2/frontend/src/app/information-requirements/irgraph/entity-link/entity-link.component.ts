import {
    AfterViewInit,
    Component,
    Inject,
    Input,
    OnInit,
    OnDestroy,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import {DOCUMENT} from "@angular/common";
import 'leader-line';
import {IRGraphComponent} from "../irgraph.component";

declare let LeaderLine: any;

@Component({
    selector: 'app-entity-link',
    templateUrl: './entity-link.component.html',
    styleUrls: ['./entity-link.component.scss']
})
export class EntityLinkComponent implements OnInit, AfterViewInit, OnDestroy, OnChanges {

    @Input() leftLink?: string;
    @Input() rightLink?: string;
    private resizeObserver: ResizeObserver = new ResizeObserver(this.onResize);
    private leaderline: any;

    constructor(@Inject(DOCUMENT) private document: any) {
    }

    ngOnChanges(changes: SimpleChanges): void {
    }

    ngOnDestroy(): void {
        if(this.leaderline!==undefined) {
            this.leaderline.remove();
        }
        const element = document.getElementById('topLevelBody');
        if(element!==null) {
            this.resizeObserver.unobserve(element);
            this.resizeObserver.disconnect();
        }
    }

    ngOnInit(): void {
        console.log("Added Entity Link Component for", this.leftLink, "to", this.rightLink);
    }

    ngAfterViewInit(): void {
        const element = document.getElementById('topLevelBody');
        if(element!==null) {
            this.resizeObserver.observe(element);
        }
        else {
            console.error("Failed to add resize observer for link changes")
        }
        if (!this.createLink()) { // not sure why, but sometimes rightlink is not found (it should have rendered by now) but a second attempt will solve it.
            this.createLink();
        }
    }

    createLink() : boolean{
        if (this.leaderline === undefined && this.leftLink!==undefined && this.rightLink!==undefined) {
            const startElement = document.getElementById(this.leftLink);
            const endElement = document.getElementById(this.rightLink);
            console.log(this.leftLink, this.rightLink, startElement, endElement)
            if (startElement != null && endElement != null) {
                this.leaderline = new LeaderLine(startElement, endElement,
                    {size: 2, color: IRGraphComponent.darkblue, startSocket: 'right', endSocket: 'left'}
                );
                return true;
            }
            else {
                console.error("Failed to add link startElement = " + startElement + ",  endElement = " + endElement)

            }
        }
        return false;
    }

    onResize(): void {
        if(this.leaderline!=null) {
            this.leaderline.position();
        }

    }
}