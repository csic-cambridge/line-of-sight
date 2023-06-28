import {DataDictionaryEntry} from "./data-dictionary-entry";
import {Oir} from "./organisational-objective";

export interface Airs {
    id: string;
    airs: string;
    oirs: Oir[];
}
