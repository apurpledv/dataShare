export interface DSFile {
    id: Number;
    path: String;
    ownerId: Number;
    name: String;
    uploadDate: Date;
    expirationDate: Date;
    type: String;
    size: Number;
    expirationLabel: String;
}