class Counter:
    typed_method_call_count = 0
    extends_count = 0
    used_classes_count = 0
    methods_count = 0
    methods_called_count = 0
    comments_count = 0
    class_instance_creation_count = 0
    literals_count = 0
    code_hints_count = 0
    unresolved_method_calls_count = 0

    def __init__(self):
        self.typed_method_call_count = 0
        self.extends_count = 0
        self.used_classes_count = 0
        self.methods_count = 0
        self.methods_called_count = 0
        self.class_instance_creation_count = 0
        self.code_hints_count = 0
        self.unresolved_method_calls_count = 0
        self.literals_count = 0
        self.comments_count = 0

    def __str__(self):
        return str(self.typed_method_call_count) + " / " \
               + str(self.extends_count) + " / " \
               + str(self.used_classes_count) + " / " \
               + str(self.methods_count) + " / " \
               + str(self.methods_called_count) + " / " \
               + str(self.class_instance_creation_count) + " / " \
               + str(self.code_hints_count) + " / " \
               + str(self.literals_count) + " / " \
               + str(self.unresolved_method_calls_count) + " / "
