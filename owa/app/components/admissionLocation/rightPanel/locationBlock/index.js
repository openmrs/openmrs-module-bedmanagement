import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./locationBlock.css');
export default class LocationBlock extends React.PureComponent {
    constructor(props, context) {
        super(props, context);

        this.intl = context.intl;
        this.urlHelper = new UrlHelper();
        this.childLocations = this.props.admissionLocationFunctions.getChildAdmissionLocations(
            this.props.admissionLocation.uuid
        );
        this.onDeleteHandler = this.onDeleteHandler.bind(this);
        this.editWardClickHandler = this.editWardClickHandler.bind(this);
        this.onClickHandler = this.onClickHandler.bind(this);
    }

    onDeleteHandler(event) {
        event.preventDefault();
        event.stopPropagation();
        const self = this;
        const confirmationMsg = this.intl.formatMessage(
            {
                id: 'DELETE_ADMISSION_LOCATION_CONFIRM_MESSAGE'
            },
            {location_name: this.props.admissionLocation.name}
        );
        const deleteSuccessMsg = this.intl.formatMessage({id: 'DELETE_SUCCESSFULLY'});
        const confirmation = confirm(confirmationMsg);
        if (confirmation) {
            axios({
                method: 'delete',
                url: this.urlHelper.apiBaseUrl() + '/admissionLocation/' + this.props.admissionLocation.uuid
            })
                .then(function() {
                    self.props.admissionLocationFunctions.notify('success', deleteSuccessMsg);
                    self.props.admissionLocationFunctions.reFetchAllAdmissionLocations();
                })
                .catch(function(errorResponse) {
                    const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                    self.props.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
                });
        }
    }

    editWardClickHandler(event) {
        event.preventDefault();
        event.stopPropagation();
        this.props.admissionLocationFunctions.setState({
            activePage: 'addEditLocation',
            pageData: {
                operation: 'edit'
            },
            activeUuid: this.props.admissionLocation.uuid
        });
    }

    onClickHandler(event) {
        event.stopPropagation();
        this.props.admissionLocationFunctions.setState({
            activeUuid: this.props.admissionLocation.uuid,
            activePage: 'listing',
            pageData: {}
        });
    }

    render() {
        const managingLocationsEnabled = this.props.admissionLocationFunctions.isManagingLocationsEnabled();
        return (
            <div className="location block" onClick={this.onClickHandler}>
                <div className="left-block">
                    <span>{this.props.admissionLocation.name}</span>
                </div>
                {managingLocationsEnabled &&
                    <ul className="right-block">
                        <li>
                            <a href="javascript:void(0);" title="edit" onClick={this.editWardClickHandler}>
                                <i className="fa fa-pencil" aria-hidden="true" />
                            </a>
                        </li>
                        {Object.keys(this.childLocations).length == 0 ? (
                            <li>
                                <a href="javascript:void(0);" title="delete" onClick={this.onDeleteHandler}>
                                    <i className="fa fa-trash" aria-hidden="true" />
                                </a>
                            </li>
                        ) : (
                            ''
                        )}
                    </ul>
                }
            </div>
        );
    }
}

LocationBlock.propTypes = {
    admissionLocation: PropTypes.object.isRequired,
    admissionLocationFunctions: PropTypes.object.isRequired
};

LocationBlock.contextTypes = {
    intl: PropTypes.object
};
