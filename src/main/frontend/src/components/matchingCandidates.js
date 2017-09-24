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

        this.likeDislikeUser = this.likeDislikeUser.bind(this);

    }


    // This function is called before render() to initialize its state.
    componentWillMount() {
        axios.get('/api/user/candidates')
            .then(({data}) => {
                console.log(data);

                this.setState({
                    matchingCandidates: data,
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

    likeDislikeUser(userObj, like) {
        let apiPath = like ? "/api/match/like" : "/api/match/dislike";
        axios.post(apiPath,
            {
                id: userObj.id
            })
            .then((data) => {
                {/*TODO Handle success message*/
                }
                let filteredArray = this.state.matchingCandidates.filter(item => item !== userObj);
                this.setState({matchingCandidates: filteredArray});

            })
            .catch(({error}) => {
                {/*TODO Better error handling*/
                }
                console.log("error during send");
                console.log(error);
                this.setState({
                    error: true
                });
            });

    }

    renderCandidates() {

        const candidateDivStyle = {
            position: 'absolute',
            width: "50%",
            backgroundColor: 'white',
            border: '1px solid black'
        };


        const likeDislikeBtnDivStlye = {};

        const likeBtnDivStyle = {};
        const dislikeBtnDivStyle = {};

        const Candidate = ({user}) => {
            return (
                <div key={user.key} id={user.key} style={candidateDivStyle}>
                    <h1>{user.profile.name}</h1>
                    <div>{user.profile.aboutMe}</div>
                    <div style={likeDislikeBtnDivStlye}>
                        <div style={likeBtnDivStyle}>
                            <button onClick={() => this.likeDislikeUser(user, true)}>Like</button>
                        </div>
                        <div style={dislikeBtnDivStyle}>
                            <button onClick={() => this.likeDislikeUser(user, false)}>Dislike</button>
                        </div>
                    </div>
                </div>);
        };

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

