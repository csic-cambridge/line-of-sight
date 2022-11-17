import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';


export function requiredNameValidator(names: string[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        let retvalue = '';
        if (typeof control.value === 'string') {
            retvalue = control.value;
        } else {
            retvalue = control.value.text;
        }

        const forbidden = !names.map(x => x).includes(retvalue);
        return forbidden ? {forbiddenName: {value: retvalue}} : null;
    };
}