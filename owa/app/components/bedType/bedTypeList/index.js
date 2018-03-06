import React from 'react';
import PropTypes from 'prop-types';

import BedTypeListRow from 'components/bedType/bedTypeList/bedTypelistRow';

require('./bedTypeList.css');
export default class BedTypeList extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.intl = context.intl;
        this.addNewHandler = this.addNewHandler.bind(this);
    }

    addNewHandler(event) {
        event.preventDefault();
        this.props.bedTypeFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTypeId: null
            }
        });
    }

    render() {
        return (
            <fieldset className="bed-type-listing">
                <legend>&nbsp; {this.intl.formatMessage({id: 'EXISTING_BED_TYPES'})} &nbsp;</legend>
                <table>
                    <thead>
                        <tr>
                            <th>{this.intl.formatMessage({id: 'NAME'})}</th>
                            <th>{this.intl.formatMessage({id: 'DISPLAY_NAME'})}</th>
                            <th className="description">{this.intl.formatMessage({id: 'DESCRIPTION'})}</th>
                            <th>{this.intl.formatMessage({id: 'ACTION'})}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.bedTypes.map((bedType, key) => (
                            <BedTypeListRow
                                key={key}
                                bedType={bedType}
                                bedTypeFunctions={this.props.bedTypeFunctions}
                            />
                        ))}
                    </tbody>
                </table>
                <button
                    onClick={this.addNewHandler}
                    value={this.intl.formatMessage({id: 'ADD_NEW'})}
                    className="list-btn">
                    {this.intl.formatMessage({id: 'ADD_NEW'})}
                </button>
            </fieldset>
        );
    }
}

BedTypeList.propTypes = {
    bedTypes: PropTypes.array.isRequired,
    bedTypeFunctions: PropTypes.object.isRequired
};

BedTypeList.contextTypes = {
    intl: PropTypes.object
};
