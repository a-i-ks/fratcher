import React from "react";

import axios from "axios";

class UserProfile extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matches: [],
            loading: true,
            error: null
        };

        // this.handleClick = this.handleClick.bind(this);
    }

    static renderLoading() {
        return <div>Loading...</div>;
    }

    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/user/match')
            .then(({data}) => {
                console.log(data);

                this.setState({
                    matches: data,
                    loading: false,
                    error: null
                });
                this.forceUpdate();
            })
            .catch(({error}) => {
                console.log("error during load");
                this.setState({
                    loading: false,
                    error: error
                });
            });
    }

    renderError() {
        return (
            <tr>
                <td>An error has occurred: {this.state.error.message}</td>
            </tr>
        );
    }

    renderMatches() {

        const Match = ({match}) => {
            return (
                <tr key={match.id} onClick={() => this.handleClick(match.id)}>
                    <td>Name</td>
                </tr>
            );
        };

        // Destructuring error and matches from state.
        // => I don't have to write "this.state...." every time.
        const {error, matches} = this.state;

        if (error) {
            return this.renderError();
        }

        if (matches.isEmpty) {
            return (<div>No matches found!</div>)
        }

        return (
            <div>
                {matches.map(match =>
                    <Match key={match.id} match={match}/>
                )}
            </div>
        );
    }

    render() {
        const {loading} = this.state;
        return (
            <div className="component">
                {loading ? this.renderLoading() :
                    <table className="table table-hover">
                        <thead>
                        <tr>
                            <th className="col-sm-5">Name</th>
                        </tr>
                        </thead>
                        <tbody>
                        {this.renderMatches()}
                        </tbody>
                    </table>}
            </div>
        );
    }
}

export default UserProfile;