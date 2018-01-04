import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';

import BedTypeListRow from 'components/bedType/bedTypeList/bedTypelistRow';
import bedTypeFunctionsMock from 'components/__mocks__/bedTypeFunctions-mock';

const testProps = {
    bedTypeFunctions: bedTypeFunctionsMock
};

describe('BedTypeListRow', () => {
    it('Should render bed Type list properly', () =>{
        const bedTypeListRow = mount(<table><tbody><BedTypeListRow bedType={testProps.bedTypeFunctions.getBedTypeById(2)}
            bedTypeFunctions={testProps.bedTypeFunctions}/></tbody></table>);

        expect(bedTypeListRow.find('td').at(0).text().trim()).toBe('luxury');
        expect(shallowToJson(bedTypeListRow)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnDeleteHandler = jest.spyOn(BedTypeListRow.prototype, 'deleteHandler');
        const spyOnEditHandler = jest.spyOn(BedTypeListRow.prototype, 'editHandler');
        const spyOnSetState = jest.spyOn(testProps.bedTypeFunctions, 'setState');
        const bedTypeList = mount(<table><tbody><BedTypeListRow bedType={testProps.bedTypeFunctions.getBedTypeById(2)}
            bedTypeFunctions={testProps.bedTypeFunctions}/></tbody></table>);

        bedTypeList.find('.fa-edit').simulate('click');
        expect(spyOnEditHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEdit',
            pageData: {
                operation: 'edit',
                bedTypeId: 2
            }
        });

        bedTypeList.find('.fa-trash').simulate('click');
        expect(spyOnDeleteHandler).toHaveBeenCalled();
    });
});