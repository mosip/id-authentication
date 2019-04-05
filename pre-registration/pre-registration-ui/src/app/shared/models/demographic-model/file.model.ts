export class FileModel {
  constructor(
    public docCatCode?: string,
    public doc_file_format?: string,
    public documentId?: string,
    public docName?: string,
    public docTypCode?: string,
    public multipartFile?: any,
    public prereg_id?: string
  ) {}
}
