import React from 'react';
import {Link} from 'react-router-dom';
import PropTypes from 'prop-types';
import {FormattedMessage} from 'react-intl';
import UrlHelper from 'utilities/urlHelper';

require('./header.css');
class Header extends React.Component {
    urlHelper = new UrlHelper();

    linkClass = (path) => {
        return this.props.path == path ? 'active' : '';
    };

    render() {
        return (
            <nav>
                <ul className="title-section">
                    <li>
                        <a href={this.urlHelper.originPath() + '/openmrs'}>
                            <i className="fa fa-home" aria-hidden="true" />
                        </a>
                    </li>
                    <li>
                        <Link
                            to={this.urlHelper.owaPath() + '/admissionLocations.html'}
                            className={this.linkClass(this.urlHelper.owaPath() + '/admissionLocations.html')}>
                            <FormattedMessage id="ADMISSION_LOCATIONS" />
                        </Link>
                    </li>
                    <li>
                        <Link
                            to={this.urlHelper.owaPath() + '/bedTypes.html'}
                            className={this.linkClass(this.urlHelper.owaPath() + '/bedTypes.html')}>
                            <FormattedMessage id="BED_TYPES" />
                        </Link>
                    </li>
                    <li>
                        <Link
                            to={this.urlHelper.owaPath() + '/bedTags.html'}
                            className={this.linkClass(this.urlHelper.owaPath() + '/bedTags.html')}>
                            <FormattedMessage id="BED_TAGS" />
                        </Link>
                    </li>
                </ul>
            </nav>
        );
    }
}

Header.contextTypes = {
    router: PropTypes.object,
    intl: PropTypes.object
};

export default Header;
