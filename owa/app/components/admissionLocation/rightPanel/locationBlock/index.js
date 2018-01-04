import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';

import UrlHelper from 'utilities/urlHelper';

require('./locationBlock.css');
export default class LocationBlock extends React.PureComponent {
    constructor(props) {
        super(props);

        this.urlHelper = new UrlHelper();
        this.childLocations = this.props.admissionLocationFunctions.getChildAdmissionLocations(this.props.admissionLocation.uuid);
        this.onDeleteHandler = this.onDeleteHandler.bind(this);
        this.editWardClickHandler = this.editWardClickHandler.bind(this);
        this.onClickHandler = this.onClickHandler.bind(this);
    }

    onDeleteHandler(event) {
        event.preventDefault();
        event.stopPropagation();
        const self = this;
        const confirmation = confirm('Are you sure you want to delete admission location '+ this.props.admissionLocation.name +'?');
        if(confirmation){
            axios({
                method: 'delete',
                url: this.urlHelper.apiBaseUrl() + '/admissionLocation/' + this.props.admissionLocation.uuid,
            }).then(function () {
                self.props.admissionLocationFunctions.notify('success', 'Delete successfully');
                self.props.admissionLocationFunctions.reFetchAllAdmissionLocations();
            }).catch(function (error) {
                self.props.admissionLocationFunctions.notify('error', error.message);
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
        return <div className="location block" onClick={this.onClickHandler}>
            <div className="left-block">
                <span>{this.props.admissionLocation.name}</span>
            </div>
            <ul className="right-block">
                <li>
                    <a href="javascript:void(0);" title="edit" onClick={this.editWardClickHandler}>
                        <i className="fa fa-pencil" aria-hidden="true"></i>
                    </a>
                </li>
                {Object.keys(this.childLocations).length == 0 ? <li>
                    <a href="javascript:void(0);" title="delete" onClick={this.onDeleteHandler}>
                        <i className="fa fa-trash" aria-hidden="true"></i>
                    </a>
                </li> : ''}
            </ul>
        </div>;
    }
}

LocationBlock.propTypes = {
    admissionLocation : PropTypes.object.isRequired,
    admissionLocationFunctions : PropTypes.object.isRequired
};