import React from 'react';
import PropTypes from 'prop-types';

import BedTagListRow from 'components/bedTag/bedTagList/bedTagListRow';

require('./bedTagList.css');
export default class BedTagList extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.intl = context.intl;
        this.addNewHandler = this.addNewHandler.bind(this);
    }

    addNewHandler(event) {
        event.preventDefault();
        this.props.bedTagFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTagId: null
            }
        });
    }

    render() {
        return (
            <fieldset className="bed-Tag-listing">
                <legend>&nbsp; {this.intl.formatMessage({id: 'EXISTING_BED_TAGS'})} &nbsp;</legend>
                <table>
                    <thead>
                        <tr>
                            <th>{this.intl.formatMessage({id: 'NAME'})}</th>
                            <th className="description">{this.intl.formatMessage({id: 'DESCRIPTION'})}</th>
                            <th>{this.intl.formatMessage({id: 'ACTION'})}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {this.props.bedTags.map((bedTag, key) => (
                            <BedTagListRow key={key} bedTag={bedTag} bedTagFunctions={this.props.bedTagFunctions} />
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

BedTagList.propTags = {
    bedTags: PropTypes.array.isRequired,
    bedTagFunctions: PropTypes.object.isRequired
};

BedTagList.contextTypes = {
    intl: PropTypes.object
};
