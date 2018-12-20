package nl.hypothermic.javacogs.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

import nl.hypothermic.javacogs.AuthenticationType;
import nl.hypothermic.javacogs.Javacogs;
import nl.hypothermic.javacogs.annotations.RequiredAuthenticationLevel;
import nl.hypothermic.javacogs.concurrency.ResponseCallback;
import nl.hypothermic.javacogs.concurrency.UncheckedCallback;
import nl.hypothermic.javacogs.entities.ArtistGroup;
import nl.hypothermic.javacogs.entities.ArtistMember;
import nl.hypothermic.javacogs.entities.Entity;
import nl.hypothermic.javacogs.entities.Label;
import nl.hypothermic.javacogs.entities.Release;
import nl.hypothermic.javacogs.entities.UserProfile;
import nl.hypothermic.javacogs.network.Response;

public class UserIdentityHandler implements IHandler {
	
	private Javacogs instance;
	
	public UserIdentityHandler(Javacogs instance) {
		this.instance = instance;
	}
	
	/**
	 * Get a user profile by username.
	 * 
	 * <pre>
	 * If authenticated as the requested user, the email key will be visible, and the num_list count will include the user’s private lists.
	 * If authenticated as the requested user or the user’s collection/wantlist is public, the num_collection / num_wantlist keys will be visible.
	 * </pre>
	 * 
	 * @param userName		The username of the user you want to request (ex. <code>rodneyfool</code>)
	 * @param cb			The callback which will be called at result time
	 * 
	 * @return UserProfile object
	 */
	@RequiredAuthenticationLevel(authType = AuthenticationType.PUBLIC)
	public void getProfileByUsername(final String userName, final ResponseCallback<UserProfile> cb) throws IOException {
		instance.threadpool.execute(new Runnable() {
			public void run() {
				try {
					cb.onResult(new Response<UserProfile>(true,
						JSON.parseObject(instance.getHttpExecutor().get(Javacogs.apiUrlBase + "users/" + userName), 
								UserProfile.class)));
				} catch (IOException x) {
					x.printStackTrace();
					cb.onResult(new Response<UserProfile>(false, null));
				}
			}
		});
	}
	
	/**
	 * Get all of the user's submissions. 
	 * This function is far from perfect and the code looks horrible, so it'll most likely be revamped soon.
	 * 
	 * @param user			UserProfile object of target user (warning: userName must not be null, it's unchecked!)
	 * @param cb			The callback which will be called at result time
	 * 
	 * @return Entity[] objects which can be casted into ArtistGroup, ArtistMember, Release, Label, etc.
	 */
	@RequiredAuthenticationLevel(authType = AuthenticationType.PROTECTED)
	public void getUserSubmissions(final UserProfile user, final UncheckedCallback<Entity[]> cb) throws IOException {
		this.getUserSubmissions(user.getUserName(), cb);
	}
	
	/**
	 * Get all of the user's submissions. 
	 * This function is far from perfect and the code looks horrible, so it'll most likely be revamped soon.
	 * 
	 * @param username		Username of the target user.
	 * @param cb			The callback which will be called at result time
	 * 
	 * @return Entity[] objects which can be casted into ArtistGroup, ArtistMember, Release, Label, etc.
	 */
	@RequiredAuthenticationLevel(authType = AuthenticationType.PROTECTED)
	public void getUserSubmissions(final String username, final UncheckedCallback<Entity[]> cb) throws IOException {
		instance.threadpool.execute(new Runnable() {
			public void run() {
				try {
					ArrayList<Entity> entities = new ArrayList<Entity>();
					JSONObject submissions = new JSONObject(instance.getHttpExecutor()
																.get(Javacogs.apiUrlBase + "users/" + username + "/submissions"))
												.getJSONObject("submissions");
					entities.addAll(JSON.parseArray(submissions.getJSONArray("artists").toString(), ArtistGroup.class));
					entities.addAll(JSON.parseArray(submissions.getJSONArray("artists").toString(), ArtistMember.class));
					
					Iterator<Entity> it = entities.iterator();
					while (it.hasNext()) {
					    Entity entity = it.next();
					    if (entity instanceof ArtistGroup) {
							if (((ArtistGroup) entity)._members == null) {
								it.remove();
							}
						} else if (entity instanceof ArtistMember) {
							if (((ArtistMember) entity).profileText == null) {
								it.remove();
							}
						}
					}
					
					entities.addAll(JSON.parseArray(submissions.getJSONArray("labels").toString(), Label.class));
					entities.addAll(JSON.parseArray(submissions.getJSONArray("releases").toString(), Release.class));
					
					cb.onResult(new Response<Entity[]>(true, entities.toArray(new Entity[] {})));
				} catch (IOException x) {
					cb.onResult(new Response<Entity[]>(false, null));
				}
			}
		});
	}
}
