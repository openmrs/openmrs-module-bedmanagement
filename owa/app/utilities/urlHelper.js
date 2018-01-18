export default class UrlHelper {
    originPath = () => {
        return window.location.origin;
    };

    fullPath = () => {
        return window.location.pathname;
    };

    owaPath = () => {
        const fullPath = window.location.pathname;
        return fullPath.substring(0, fullPath.lastIndexOf('/'));
    };

    apiBaseUrl = () => {
        return window.location.origin + '/openmrs/ws/rest/v1';
    };
}
