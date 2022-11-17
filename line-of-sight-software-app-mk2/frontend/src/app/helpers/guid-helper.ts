export class GuidHelper {
    static getGuid(): string {
        const uuid = self.crypto.randomUUID();
        return uuid;
    }
}
declare global {
    interface Crypto {
        randomUUID: () => string;
    }
}

export {};
