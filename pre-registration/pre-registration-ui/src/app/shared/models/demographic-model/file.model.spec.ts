import { FileModel } from './file.model';

describe('FileModel check', () => {
  it('should conform to FileModel', () => {
    const fileModel = new FileModel('fre', 'franch');

    expect(typeof fileModel.doc_cat_code).toEqual('string');
  });
});
