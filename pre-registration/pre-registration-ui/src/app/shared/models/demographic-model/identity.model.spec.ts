import { FileModel } from './file.model';
import { IdentityModel } from './identity.modal';
import { AttributeModel } from './attribute.modal';

describe('FileModel check', () => {
  it('should conform to FileModel', () => {
    const attributeModel = new AttributeModel('fre', 'franch');
    const identity = new IdentityModel(
      1,
      [attributeModel],
      'string',
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      [attributeModel],
      'string',
      'string',
      'string',
      'string'
    );
    expect(typeof identity.fullName).toEqual('object');
  });
});
