export interface UploadFile {
    file: Blob;
    userId: number;
    expirationDate: number;
    password: string;
}