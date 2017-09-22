import React from "react";

import axios from "axios";

/**
 * I used the following tutorial as a guide, how to how and where to fetch data:
 * https://daveceddia.com/ajax-requests-in-react/
 */

class MatchingCandidates extends React.Component {

    constructor(props) {
        super();
        this.state = {
            matchingCandidates: [],
            loading: true,
            error: null
        };
        console.log("MatchingCandidates constructor");

    }


    // This function is called before render() to initialize its state.
    componentWillMount() {
        console.log("componentWillMount ....");
        axios.get('/api/user/candidates')
            .then(({data}) => {
                console.log(data);

                const matchingCandidates = data;

                this.setState({
                    matchingCandidates,
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

    renderLoading() {
        return <div>Loading...</div>;
    }

    renderError() {
        return (
            <div>
                An error has occurred: {this.state.error.message}
            </div>
        );
    }

    renderCandidates() {

        const candidateDivStyle = {
            position: 'absolute',
            backgroundColor: 'white'
        };

        const Candidate = ({user}) => {
            console.log("Candidate const");
            console.log("username=" + user.username);
            return (
                <div key={user.key} style={candidateDivStyle}>
                    <h1>Hello, {user.username}!</h1>
                </div>);
        };

        console.log("renderCandidates ...");
        // Destructuring error and candidates from state.
        // => I don't have to write "this.state...." every time.
        const {error, matchingCandidates} = this.state;

        if (error) {
            return this.renderError();
        }

        if (matchingCandidates.isEmpty) {
            return (<div>No candidates found!</div>)
        }

        return (
            <div>
                {matchingCandidates.map(candidate =>
                    <Candidate key={candidate.id} user={candidate}/>
                )}
            </div>
        );
    }

    render() {
        const {loading} = this.state;
        return (
            <div>
                {loading ? this.renderLoading() : this.renderCandidates()}
            </div>
        );
    }
}

export default MatchingCandidates;

