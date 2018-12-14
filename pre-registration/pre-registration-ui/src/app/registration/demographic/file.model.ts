export class FileModel {
  constructor(
    public doc_cat_code: string,
    public doc_file_format: string,
    public doc_id: string,
    public doc_name: string,
    public doc_typ_code: string,
    public multipartFile: any,
    public prereg_id: string
  ) {}
}
