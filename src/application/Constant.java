package application;


public class Constant {
	public static String req_online_notification_type = "hreq-inform-online";
	public static String req_send_message_type = "hreq-send-message";
	public static String req_inquire_presence = "hreq-inquire-presence";
	public static String req_get_messages_count = "hreq-get-messages-count";
	public static String req_get_messages = "hreq-get-messages";
	public static String req_create_group = "hreq-create-group";
	public static String req_send_message_group = "hreq-send-group-message";
	public static String req_get_message_group_count = "hreq-get-group-messages-count";
	public static String req_get_group_messages = "hreq-get-group-messages";
	public static String req_mark_messages_seen = "hreq-mark-messages-seen";
	public static String req_mark_group_messages_seen = "hreq-mark-group-messages-seen";
	public static String req_send_call_type = "hreq-call-message";
	public static String req_add_user_to_group = "hreq-add-user-to-group";
	public static String req_leave_group = "hreq-leave-group";
	
	
	
	public static String res_message_notification = "msg-notification";
	public static String res_new_message_notification = "msg-new-messages";
	public static String res_success = "hres-success";
	public static String res_error = "hreq-error";
	public static String res_presence = "msg-online-list";
	public static String res_get_message_count = "hres-get-messages-count";
	public static String res_get_messages = "hres-get-messages";
	public static String res_notification_new_group = "msg-notification-new-group";
	public static String res_create_group = "hres-create-group";
	public static String res_group_notification = "msg-group-notification";
	public static String res_get_message_group_count = "hres-get-group-messages-count";
	public static String res_get_messages_group = "hres-get-group-messages";
	public static String res_mark_messages_seen = "hres-mark-messages-seen";
	public static String res_mark_group_messages_seen = "hres-mark-group-messages-seen";
	public static String res_call_notification = "call-notification";
	public static String res_notification_added_to_group = "msg-notification-added-to-group";
	public static String res_notification_new_paticipant = "msg-notification-new-participant";
	public static String res_notification_leave_group = "msg-notification-leave-group";
	
	
	public static String res_msg_account_data = "msg-account-data";
	
	private static String title_new_message = "New Message From: ";
	private static String title_error = "Error: ";
	private static String online_status = "Online";
	private static String offline_status = "Last Online At: ";
	private static String status = "STATUS: ";
	private static String title_new_call = "New Call From: ";     

	
	public static String create_title_new_message(String title) {
		return title_new_message +title;
	}
	public static String create_title_error(String title) {
		return title_error + title;
	}
	public static String create_status(String time) {
		if(time.equals("Online")) return status + online_status;
		else return offline_status+time;
	}
	public static String create_title_new_call(String title) {
		return title_new_call +title;                                        
	}
	
	
}
