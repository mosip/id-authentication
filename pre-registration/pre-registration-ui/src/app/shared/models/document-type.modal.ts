export class DocumentTypeModel {
  constructor(
    public code: string,
    public description: string,
    public isActive: string,
    public langCode: string,
    public name: string,
    public documentTypes?: DocumentTypeModel[]
  ) {}
}
