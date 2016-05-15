package ee.valja7.gate;

class DevelopmentGuiceModule extends GuiceModule {
    @Override
    protected String getModemPortName() {
        return "/dev/ttyUSB2";
    }
}
