import {DataDictionaryEntry} from "./data-dictionary-entry";

export interface FunctionalObjective {
    id: string;
    data_dictionary_entry: DataDictionaryEntry;
    firs: string[];
    assets: string[];
}
