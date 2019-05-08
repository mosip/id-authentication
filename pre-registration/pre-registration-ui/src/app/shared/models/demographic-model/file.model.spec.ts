import { FileModel } from './file.model';

describe('FileModel check', () => {
  it('should conform to FileModel', () => {
    const fileModel = new FileModel('fre', 'franch');

    expect(typeof fileModel.docCatCode).toEqual('string');
  });
});
