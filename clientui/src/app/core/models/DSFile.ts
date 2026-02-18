export interface DSFile {
    id: number;
    path: string;
    ownerId: number;
    name: string;
    uploadDate: Date;
    expirationDate: Date;
    type: string;
    size: number;
    expirationLabel: string;
    sizeLabel: string;
}