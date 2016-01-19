msg = ["This", "Is", "A", "Message", "Delivered", "Over", "Time"]
index = 0

def setup():
	pass

def retrieveData():
	global msg, index
	s = msg[index]
	index += 1
	if index >= len(msg):
		index = 0
	return s
	